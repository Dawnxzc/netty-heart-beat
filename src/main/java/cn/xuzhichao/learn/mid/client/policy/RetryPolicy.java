package cn.xuzhichao.learn.mid.client.policy;

/**
 * 重试策略接口
 * @author xuzhichao
 * @date 2019/7/8 10:34
 * @Description:
 */
public interface RetryPolicy {

    /**
     * 重试方法
     *
     * 操作失败时被调用
     * @param retryCount 重试次数
     * @return true 需要尝试
     */
    boolean allowRetry(int retryCount);

    /**
     * 获取当前重试次数的休眠时间（ms）
     * @param retryCount 重试次数
     * @return
     */
    long getSleepTimeMs(int retryCount);
}
