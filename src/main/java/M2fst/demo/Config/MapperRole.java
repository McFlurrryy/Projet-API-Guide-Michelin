package M2fst.demo.Config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class MapperRole implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        // En regardant le token avec jwt.io, on constate que le role se trouve dans "ressource_access".
        // Il faut donc récupérer le role en mappant.
        Map<String, Object> resource_access = source.getClaimAsMap("resource_access");
        if (resource_access == null || resource_access.isEmpty() || !resource_access.containsKey("coursm2")) {
            return Collections.emptyList();
        }

        Map<String, Collection<String>> coursm2 = (Map<String, Collection<String>>) resource_access.get("coursm2");
        if (coursm2 == null || coursm2.isEmpty() || !coursm2.containsKey("roles")) {
            return Collections.emptyList();
        }

        List<String> roles = (List<String>) coursm2.get("roles");
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        System.out.println(roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList()));

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }
}
