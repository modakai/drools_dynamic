// API相关类型定义

import type { AxiosRequestConfig, AxiosResponse } from 'axios'

/**
 * HTTP方法枚举
 */
export enum HttpMethod {
    GET = 'GET',
    POST = 'POST',
    PUT = 'PUT',
    DELETE = 'DELETE',
    PATCH = 'PATCH'
}

/**
 * HTTP状态码枚举
 */
export enum HttpStatus {
    OK = 200,
    CREATED = 201,
    NO_CONTENT = 204,
    BAD_REQUEST = 400,
    UNAUTHORIZED = 401,
    FORBIDDEN = 403,
    NOT_FOUND = 404,
    METHOD_NOT_ALLOWED = 405,
    CONFLICT = 409,
    INTERNAL_SERVER_ERROR = 500,
    BAD_GATEWAY = 502,
    SERVICE_UNAVAILABLE = 503
}

/**
 * API错误类型
 */
export interface ApiError {
    code: string
    message: string
    details?: Record<string, any>
    timestamp: string
    path: string
}

/**
 * 请求配置扩展类型
 */
export interface RequestConfig extends AxiosRequestConfig {
    showLoading?: boolean
    showError?: boolean
    errorMessage?: string
}

/**
 * 响应拦截器配置类型
 */
export interface ResponseInterceptorConfig {
    onSuccess?: (response: AxiosResponse) => AxiosResponse
    onError?: (error: any) => Promise<any>
}

/**
 * 请求拦截器配置类型
 */
export interface RequestInterceptorConfig {
    onFulfilled?: (config: AxiosRequestConfig) => AxiosRequestConfig
    onRejected?: (error: any) => Promise<any>
}

/**
 * API客户端配置类型
 */
export interface ApiClientConfig {
    baseURL: string
    timeout: number
    headers?: Record<string, string>
    withCredentials?: boolean
    requestInterceptor?: RequestInterceptorConfig
    responseInterceptor?: ResponseInterceptorConfig
}

/**
 * 上传文件进度类型
 */
export interface UploadProgress {
    loaded: number
    total: number
    percentage: number
}

/**
 * 文件上传配置类型
 */
export interface UploadConfig {
    url: string
    file: File
    data?: Record<string, any>
    headers?: Record<string, string>
    onProgress?: (progress: UploadProgress) => void
    onSuccess?: (response: any) => void
    onError?: (error: any) => void
}

/**
 * 下载文件配置类型
 */
export interface DownloadConfig {
    url: string
    filename?: string
    params?: Record<string, any>
    headers?: Record<string, string>
    onProgress?: (progress: UploadProgress) => void
    onSuccess?: () => void
    onError?: (error: any) => void
}