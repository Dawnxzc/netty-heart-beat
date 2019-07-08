package cn.xuzhichao.learn.mid.client.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * 重试策略默认实现
 * @author xuzhichao
 * @date 2019/7/8 10:39
 * @Description:
 */
public class ExponentialBackOffRetry implements RetryPolicy {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRIES_LIMIT = 29;

    /**
     * 默认最长休眠时间
     */
    private static final int DEFAULT_MAX_SLEEP_MS = Integer.MAX_VALUE;

    private final Random random = new Random();
    private final long baseSleepTimeMs;
    private final int maxRetries;
    private final int maxSleepMs;

    public ExponentialBackOffRetry(int baseSleepTimeMs, int maxRetries, int maxSleepMs){
        this.baseSleepTimeMs = baseSleepTimeMs;
        this.maxRetries = maxRetries;
        this.maxSleepMs = maxSleepMs;
    }

    public ExponentialBackOffRetry(int baseSleepTimeMs, int maxRetries) {
        this(baseSleepTimeMs, maxRetries, DEFAULT_MAX_SLEEP_MS);
    }

    @Override
    public boolean allowRetry(int retryCount) {
        if (retryCount < maxRetries){
            return true;
        }
        return false;
    }

    @Override
    public long getSleepTimeMs(int retryCount) {
        if (retryCount < 0) {
            throw new IllegalArgumentException("重试次数必须大于 0");
        }
        if (retryCount > MAX_RETRIES_LIMIT) {
            logger.info(String.format("最大重试次数太大(%d)，修改为上限值(%d)", maxRetries, MAX_RETRIES_LIMIT));
            retryCount = MAX_RETRIES_LIMIT;
        }
        long sleepMs = baseSleepTimeMs * Math.max(1, random.nextInt(1 << retryCount));
        if (sleepMs > maxSleepMs) {
            logger.info(String.format("睡眠时间太大 (%d)，调整为上限值 (%d)", sleepMs, maxSleepMs));
            sleepMs = maxSleepMs;
        }
        return sleepMs;
    }
}
