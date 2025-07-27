package com.example.drools.service;

import com.example.drools.dto.RuleFactConfig;
import com.example.drools.dto.TestResult;
import com.example.drools.dto.TestRuleRequest;
import com.example.drools.entity.DroolsRule;
import com.example.drools.repository.DroolsRuleRepository;
import com.example.drools.utils.ReflectionUtil;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Drools规则测试服务
 * 提供规则执行和测试功能
 *
 * @author System
 * @since 1.0.0
 */
@Service
public class DroolsRuleTestService {

    private static final Logger logger = LoggerFactory.getLogger(DroolsRuleTestService.class);

    private static final String TEST_RULES_PATH = "src/main/resources/test/";
    private static final long DEFAULT_TIMEOUT_MS = 3000000L; // 30秒默认超时

    private final DroolsRuleRepository droolsRuleRepository;

    // 线程池用于执行测试任务
    private final ExecutorService executorService = Executors.newCachedThreadPool(r -> {
        Thread thread = new Thread(r, "drools-test-" + System.currentTimeMillis());
        thread.setDaemon(true);
        return thread;
    });

    public DroolsRuleTestService(DroolsRuleRepository droolsRuleRepository) {

        this.droolsRuleRepository = droolsRuleRepository;
    }

    /**
     * 执行规则测试
     *
     * @param request 测试请求
     * @return 测试结果
     */
    public TestResult executeTest(TestRuleRequest request) {
        logger.info("开始执行规则测试");

        long startTime = System.currentTimeMillis();

        try {
            // 验证请求参数
            validateTestRequest(request);

            // 确定测试超时时间
            long timeoutMs = request.getMaxExecutionTime() != null ?
                    request.getMaxExecutionTime() : DEFAULT_TIMEOUT_MS;

            // 创建测试任务
            Callable<TestResult> testTask = () -> {
                // 测试指定的规则ID列表
                return executeTestWithRuleIds(request);
            };

            // 执行测试任务（带超时控制）
            Future<TestResult> future = executorService.submit(testTask);
//            TestResult result = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            TestResult result = future.get();

            long executionTime = System.currentTimeMillis() - startTime;
            result.setExecutionTime(executionTime);

            logger.info("规则测试执行完成，耗时: {} ms", executionTime);
            return result;

        }/* catch (TimeoutException e) {
            logger.error("规则测试执行超时");
            return new TestResult("规则测试执行超时", "Execution timeout after " + 
                                (request.getMaxExecutionTime() != null ? request.getMaxExecutionTime() : DEFAULT_TIMEOUT_MS) + " ms");
        }*/ catch (Exception e) {
            logger.error("规则测试执行失败", e);
            return new TestResult(e.getMessage(), getStackTrace(e));
        }
    }


    /**
     * 使用规则ID列表执行测试
     */
    private TestResult executeTestWithRuleIds(TestRuleRequest request) {
        logger.debug("使用规则ID列表执行测试: {}", request.getRuleIds());

        try {
            // 查询指定的规则
            List<DroolsRule> rules = new ArrayList<>();
            for (Long ruleId : request.getRuleIds()) {
                DroolsRule rule = droolsRuleRepository.findById(ruleId);
                if (rule != null && rule.getEnabled()) {
                    rules.add(rule);
                } else {
                    logger.warn("规则不存在或已禁用: ID {}", ruleId);
                }
            }

            if (rules.isEmpty()) {
                return new TestResult("没有找到有效的规则进行测试", "No valid rules found for testing");
            }

            // 创建包含指定规则的测试容器
            KieContainer testContainer = createTestContainerWithRules(rules);

            // 执行规则
            return executeRulesWithContainer(testContainer, request);

        } catch (Exception e) {
            logger.error("使用规则ID列表执行测试失败", e);
            throw new RuntimeException("规则测试失败: " + e.getMessage(), e);
        }
    }


    /**
     * 使用指定容器执行规则
     */
    private TestResult executeRulesWithContainer(KieContainer container, TestRuleRequest request) {
        logger.debug("开始执行规则");

        KieSession kieSession = null;
        try {
            // 创建KieSession
            kieSession = container.newKieSession();

            // 创建规则执行监听器
            TestRuleExecutionListener listener = new TestRuleExecutionListener(request.getVerbose());
            kieSession.addEventListener(listener);

            // 插入测试数据到工作内存
            /*
                todo
                    这里肯定存在问题，每个drl文件的$变量名都不一样，这个通过默认的insert去插入，kieSession匹配规则会根据对应插入的Fact对象
                    来进行匹配，如果匹配不到对应的类型，也就不会执行规则

               todo 可以通过Java的反射来完成，不过rule表要多记一个字段，用于记录反射要生成的类(全类名例如：com.sakura.drools.domain.Order)
                    然后前端配置对应的字段名和字段值，来进行赋值，最后丢反射生成的对象给kieSession
             */
            // 优先使用规则配置创建对象
            if (request.getRuleConfigs() != null && !request.getRuleConfigs().isEmpty()) {
                // 使用反射创建对象并插入
                for (RuleFactConfig config : request.getRuleConfigs()) {
                    Object factInstance = ReflectionUtil.createInstance(
                            config.getClassName(),
                            config.getFieldValues()
                    );
                    kieSession.insert(factInstance);
                    logger.debug("插入事实对象: {} = {}", config.getFactName(), factInstance);
                }
            } else {
                TestResult result = new TestResult("无Fact对象", "未传递Fact对象");
                result.setSuccess(false);
                result.setTestTime(LocalDateTime.now());
                return result;
            }

            // 记录执行开始时间
            long startTime = System.currentTimeMillis();

            // 执行规则
            int firedRulesCount = kieSession.fireAllRules();

            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;

            // 收集执行结果
            Map<String, Object> resultData = collectResultData(kieSession);

            // 创建测试结果
            TestResult result = new TestResult(
                    executionTime,
                    firedRulesCount,
                    listener.getFiredRules(),
                    resultData
            );

            // 设置详细执行信息
            if (request.getVerbose()) {
                result.setExecutionDetails(listener.getExecutionDetails());
            }

            logger.debug("规则执行完成: 触发 {} 条规则，耗时 {} ms", firedRulesCount, executionTime);
            return result;

        } catch (Exception e) {
            logger.error("执行规则失败", e);
            throw new RuntimeException("规则执行失败: " + e.getMessage(), e);
        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }

    /**
     * 创建包含多个规则的测试容器
     */
    private KieContainer createTestContainerWithRules(List<DroolsRule> rules) {
        logger.debug("创建包含 {} 条规则的测试容器", rules.size());

        try {
            KieServices kieServices = KieServices.Factory.get();
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

            // 添加所有规则到文件系统
            for (int i = 0; i < rules.size(); i++) {
                DroolsRule rule = rules.get(i);
                String rulePath = TEST_RULES_PATH + "test_rule_" + i + ".drl";
                kieFileSystem.write(rulePath, rule.getRuleContent());
            }

            // 构建容器
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();

            Results results = kieBuilder.getResults();
            if (results.hasMessages(Message.Level.ERROR)) {
                StringBuilder errorMsg = new StringBuilder("规则编译错误:\n");
                for (Message message : results.getMessages(Message.Level.ERROR)) {
                    errorMsg.append("- ").append(message.getText()).append("\n");
                }
                throw new RuntimeException(errorMsg.toString());
            }

            KieModule kieModule = kieBuilder.getKieModule();
            return kieServices.newKieContainer(kieModule.getReleaseId());

        } catch (Exception e) {
            logger.error("创建测试容器失败", e);
            throw new RuntimeException("创建测试容器失败: " + e.getMessage(), e);
        }
    }

    /**
     * 收集执行结果数据
     */
    private Map<String, Object> collectResultData(KieSession kieSession) {
        logger.debug("收集执行结果数据");

        Map<String, Object> resultData = new HashMap<>();

        // 获取工作内存中的所有对象
        Collection<?> objects = kieSession.getObjects();

        int index = 0;
        for (Object obj : objects) {
            if (obj instanceof TestDataObject dataObj) {
                resultData.put("result_" + index, dataObj.data());
            } else {
                resultData.put("object_" + index, obj);
            }
            index++;
        }

        return resultData;
    }

    /**
     * 验证测试请求
     */
    private void validateTestRequest(TestRuleRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("测试请求不能为空");
        }

        if ((request.getRuleIds() == null || request.getRuleIds().isEmpty())) {
            throw new IllegalArgumentException("必须提供规则ID列表");
        }

        if (request.getMaxExecutionTime() != null && request.getMaxExecutionTime() <= 0) {
            throw new IllegalArgumentException("最大执行时间必须大于0");
        }
    }

    /**
     * 获取异常堆栈跟踪
     */
    private String getStackTrace(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public void saveTestResult() {

    }

    /**
         * 测试数据对象
         */
        public record TestDataObject(Map<String, Object> data) {
            public TestDataObject(Map<String, Object> data) {
                this.data = new HashMap<>(data);
            }


        public Object get(String key) {
                return data.get(key);
            }

            public void set(String key, Object value) {
                data.put(key, value);
            }

            public boolean has(String key) {
                return data.containsKey(key);
            }

            @Override
            public String toString() {
                return "TestDataObject{" + "data=" + data + '}';
            }
        }

    /**
     * 规则执行监听器
     */
    private static class TestRuleExecutionListener implements RuleRuntimeEventListener {

        private final boolean verbose;
        private final List<TestResult.FiredRuleInfo> firedRules = new ArrayList<>();
        private final StringBuilder executionDetails = new StringBuilder();

        public TestRuleExecutionListener(boolean verbose) {
            this.verbose = verbose;
        }

        @Override
        public void objectInserted(ObjectInsertedEvent event) {
            if (verbose) {
                executionDetails.append("对象插入: ").append(event.getObject()).append("\n");
            }
        }

        @Override
        public void objectUpdated(ObjectUpdatedEvent event) {
            if (verbose) {
                executionDetails.append("对象更新: ").append(event.getObject()).append("\n");
            }
        }

        @Override
        public void objectDeleted(ObjectDeletedEvent event) {
            if (verbose) {
                executionDetails.append("对象删除: ").append(event.getOldObject()).append("\n");
            }
        }

        public List<TestResult.FiredRuleInfo> getFiredRules() {
            return firedRules;
        }

        public String getExecutionDetails() {
            return executionDetails.toString();
        }
    }
}