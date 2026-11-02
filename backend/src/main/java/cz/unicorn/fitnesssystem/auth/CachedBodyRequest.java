package cz.unicorn.fitnesssystem.auth;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import lombok.Getter;

/**
 * Reads the whole body up front so the signature filter can hash it before the controller
 * consumes it ({@code ContentCachingRequestWrapper} only caches after the body is read).
 */
public class CachedBodyRequest extends HttpServletRequestWrapper {

    @Getter
    private final byte[] body;

    public CachedBodyRequest(HttpServletRequest request) throws IOException {
        super(request);
        this.body = request.getInputStream().readAllBytes();
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream in = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() {
                return in.read();
            }

            @Override
            public int read(byte[] b, int off, int len) {
                return in.read(b, off, len);
            }

            @Override
            public boolean isFinished() {
                return in.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }
}