package org.lorislab.lorisgate.rs.admin.v1.mappers;

import java.util.Collection;
import java.util.List;

import org.lorislab.lorisgate.domain.model.Client;
import org.lorislab.lorisgate.domain.model.Realm;
import org.lorislab.lorisgate.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.org.lorislab.lorisgate.rs.admin.v1.model.ClientDTO;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.RealmDTO;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.RealmItemDTO;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.UserDTO;

@Mapper
public interface RealmMapper {

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

    @Mapping(target = "removeRolesItem", ignore = true)
    UserDTO map(User user);
}
