package com.tnbook.tnbook.service.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Optional;

public class CookieUtils {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }

        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static String serialize(Object object) {
        return Base64
                .getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        if (cookie == null || cookie.getValue() == null || cookie.getValue().trim().isEmpty()) {
            return null;
        }

        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(cookie.getValue());
            try (ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                return cls.cast(ois.readObject());
            }
        } catch (IllegalArgumentException | ClassCastException e) {
            System.err.println("Failed to deserialize cookie '" + cookie.getName() + "': " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error deserializing cookie '" + cookie.getName() + "': " + e.getMessage());
            return null;
        }
    }

}
