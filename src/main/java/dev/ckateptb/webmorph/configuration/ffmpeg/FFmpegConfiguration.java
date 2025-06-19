package dev.ckateptb.webmorph.configuration.ffmpeg;

import jakarta.annotation.PostConstruct;
import org.bytedeco.ffmpeg.global.avutil;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class that disables FFmpeg's native logging output at application startup.
 *
 * <p>This class configures the FFmpeg native library (via the JavaCPP bindings from {@code bytedeco})
 * to suppress all internal log messages by setting the log level to {@code AV_LOG_QUIET}.</p>
 *
 * <p>By default, FFmpeg may emit diagnostic or debug logs to standard error during library usage,
 * which can clutter application output or interfere with structured logging systems.</p>
 *
 * <p>This configuration is applied in a {@link jakarta.annotation.PostConstruct} method to ensure
 * that the suppression is in effect before any FFmpeg components are used by the application.</p>
 *
 * @see <a href="https://github.com/bytedeco/javacpp-presets/tree/master/ffmpeg">JavaCPP FFmpeg bindings</a>
 * @see avutil#av_log_set_level(int)
 */

@Configuration
public class FFmpegConfiguration {
    @PostConstruct
    public void mute() {
        avutil.av_log_set_level(avutil.AV_LOG_QUIET);
    }
}
