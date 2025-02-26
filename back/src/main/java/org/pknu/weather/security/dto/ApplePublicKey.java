package org.pknu.weather.security.dto;

public record ApplePublicKey(String kty,
                             String kid,
                             String alg,
                             String n,
                             String e) {
}
