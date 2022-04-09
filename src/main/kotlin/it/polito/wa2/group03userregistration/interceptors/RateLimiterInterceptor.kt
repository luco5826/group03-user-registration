package it.polito.wa2.group03userregistration.interceptors

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import io.github.bucket4j.local.LocalBucket
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class RateLimiterInterceptor : HandlerInterceptor {

    private val bucket: LocalBucket = Bucket
        .builder()
        .addLimit(
            Bandwidth
                .classic(10, Refill.intervally(10, java.time.Duration.ofMinutes(1)))
        )
        .build()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        return if (bucket.tryConsume(1)) {
            true
        } else {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value())
            false
        }

    }
}