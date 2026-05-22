package publication_quality_system.services;

import java.util.Set;

public interface CognitoGroupService {
    void ensureGroupExists(String groupName);

    void createGroup(String groupName, String description);

    void updateGroup(String oldGroupName, String newGroupName, String description);

    void deleteGroup(String groupName);

    void addUserToGroup(String username, String groupName);

    void removeUserFromGroup(String username, String groupName);

    Set<String> getUserGroups(String username);
}
