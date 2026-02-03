package org.lorislab.lorisgate.rs.admin.v1.mappers;

import java.util.Collection;
import java.util.List;

import org.lorislab.lorisgate.domain.model.Client;
import org.lorislab.lorisgate.domain.model.Realm;
import org.lorislab.lorisgate.domain.model.Role;
import org.lorislab.lorisgate.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.org.lorislab.lorisgate.rs.admin.v1.model.*;

@Mapper
public interface RealmMapper {

    default Realm create(RealmDTO dto) {
        var realm = createItems(dto);
        if (realm == null) {
            return null;
        }
        dto.getRoles().forEach((key, value) -> realm.addRole(create(key, value)));
        dto.getClients().forEach((key, value) -> realm.addClient(create(key, value)));
        dto.getUsers().forEach((key, value) -> realm.addUser(create(key, value)));
        return realm;
    }

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "clients", ignore = true)
    Realm createItems(RealmDTO dto);

    Client create(String clientId, ClientDTO dto);

    User create(String username, UserDTO dto);

    Role create(String name, RoleDTO dto);

    List<ClientDTO> mapClients(Collection<Client> items);

    default ClientSearchResultDTO mapResultClients(Collection<Client> items) {
        var r = new ClientSearchResultDTO();
        r.setItems(mapClients(items));
        return r;
    }

    List<UserDTO> mapUsers(Collection<User> items);

    default UserSearchResultDTO mapResultUsers(Collection<User> items) {
        var r = new UserSearchResultDTO();
        r.setItems(mapUsers(items));
        return r;
    }

    default RealmSearchResultDTO mapResult(Collection<Realm> items) {
        var r = new RealmSearchResultDTO();
        r.setItems(mapItems(items));
        return r;
    }

    List<RealmItemDTO> mapItems(Collection<Realm> realms);

    RealmItemDTO mapItem(Realm realm);

    @Mapping(target = "removeRolesItem", ignore = true)
    @Mapping(target = "removeClientsItem", ignore = true)
    @Mapping(target = "removeUsersItem", ignore = true)
    RealmDTO map(Realm realm);

    @Mapping(target = "removeRolesItem", ignore = true)
    @Mapping(target = "removeScopesItem", ignore = true)
    @Mapping(target = "removeRedirectUrisItem", ignore = true)
    ClientDTO map(Client client);

    @Mapping(target = "removeGroupsItem", ignore = true)
    @Mapping(target = "removeRolesItem", ignore = true)
    UserDTO map(User user);
}
