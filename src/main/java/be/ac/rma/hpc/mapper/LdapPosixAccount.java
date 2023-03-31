/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.ac.rma.hpc.mapper;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.idm.query.internal.LDAPQuery;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;
import java.util.Collections;
import java.util.Set;

/**
 * @author Bart Janssens
 */
public class LdapPosixAccount extends AbstractLDAPStorageMapper {

    private static final Logger logger = Logger.getLogger(LdapPosixAccount.class);
    public static final String LDAP_NEXT_UID = "ldap.next.uid";
    public static final String LDAP_POSIX_UID_ATTRIBUTE_NAME = "uidNumber";

    public LdapPosixAccount(ComponentModel mapperModel, LDAPStorageProvider ldapProvider) {
        super(mapperModel, ldapProvider);
    }

    @Override
    public void onImportUserFromLDAP(LDAPObject ldapUser, UserModel user, RealmModel realm, boolean isCreate) {
        // We don't do anything here, if propagation from LDAP to KeyCloak is needed then it should be done using 
        // the standard UserAttributeLDAPStorageMapper
        logger.warn("importing ldap user" + ldapUser.toString());
    }

    @Override
    public void onRegisterUserToLDAP(LDAPObject ldapUser, UserModel localUser, RealmModel realm) {
        logger.warn("storing ldap user" + ldapUser.toString());
        ldapUser.setSingleAttribute(LDAP_POSIX_UID_ATTRIBUTE_NAME, getUid());
        updateUid(realm);
    }

    @Override
    public Set<String> mandatoryAttributeNames() {
        return Collections.singleton(LDAP_POSIX_UID_ATTRIBUTE_NAME);
    }

    @Override
    public UserModel proxy(final LDAPObject ldapUser, UserModel delegate, RealmModel realm) {
        return delegate;
    }

    @Override
    public void beforeLDAPQuery(LDAPQuery query) {
    }

    private String getUid() {
        return mapperModel.getConfig().getFirst(LDAP_NEXT_UID);
    }

    private void updateUid(RealmModel realm) {
        int next_uid = Integer.parseInt(getUid()) + 1;
        mapperModel.getConfig().putSingle(LDAP_NEXT_UID, String.valueOf(next_uid));
        realm.updateComponent(mapperModel);
    }
}
