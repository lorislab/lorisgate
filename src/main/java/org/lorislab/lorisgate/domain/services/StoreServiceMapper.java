package org.lorislab.lorisgate.domain.services;

import org.lorislab.lorisgate.domain.model.Client;
import org.lorislab.lorisgate.domain.model.Realm;
import org.lorislab.lorisgate.domain.model.Role;
import org.lorislab.lorisgate.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.org.lorislab.lorisgate.rs.admin.v1.model.ClientDTO;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.RealmDTO;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.RoleDTO;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.UserDTO;

@Mapper
public interface StoreServiceMapper {

    default Realm create(RealmDTO dto) {
        var realm = createRealm(dto);
        if (realm == null) {
            return null;
        }
        dto.getRoles().forEach((key, value) -> realm.addRole(mapRole(key, value)));
        dto.getClients().forEach((key, value) -> realm.addClient(mapClient(key, value)));
        dto.getUsers().forEach((key, value) -> realm.addUser(mapUser(key, value)));
        return realm;
    }

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "clients", ignore = true)
    Realm createRealm(RealmDTO dto);

    Role mapRole(String key, RoleDTO role);

    Client mapClient(String key, ClientDTO client);

    User mapUser(String key, UserDTO user);
}
