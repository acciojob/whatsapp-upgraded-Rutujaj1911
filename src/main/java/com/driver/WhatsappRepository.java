package com.driver;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WhatsappRepository {

     private HashMap<Group, List<User>> groupUserMap;
     private HashMap<Group, List<Message>> groupMessageMap;
     private HashMap<Message, User> senderMap;
     private HashMap<Group, User> adminMap;
     private HashMap<String, User> userData;
     private int customGroupCount;
     private int messageId;

     public WhatsappRepository(){
          this.groupMessageMap = new HashMap<Group, List<Message>>();
          this.groupUserMap = new HashMap<Group, List<User>>();
          this.senderMap = new HashMap<Message, User>();
          this.adminMap = new HashMap<Group, User>();
          this.userData = new HashMap<>();

          this.customGroupCount = 0;
          this.messageId = 0;
     }

     public boolean isUser(String mobile) {
          if(userData.containsKey(mobile)) return false;
          return true;
     }

     public void createUser(String name, String mobile) {
          userData.put(mobile, new User(name, mobile));
     }

     public String changeAdmin(User approver, User user, Group group) throws Exception{
          //Throw "Group does not exist" if the mentioned group does not exist
          //Throw "Approver does not have rights" if the approver is not the current admin of the group
          //Throw "User is not a participant" if the user is not a part of the group
          //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.

          if(!groupUserMap.containsKey(group)) throw new Exception("Group does not exist");
          if(!adminMap.get(group).equals(approver)) throw new Exception("Approver does not have rights");
          if(!this.userExistsInGroup(group, user)) throw  new Exception("User is not a participant");

          adminMap.put(group, user);
          return "SUCCESS";
     }

     public Group createGroup(List<User> users) {
          // The list contains at least 2 users where the first user is the admin. A group has exactly one admin.
          // If there are only 2 users, the group is a personal chat and the group name should be kept as the name of the second user(other than admin)
          // If there are 2+ users, the name of group should be "Group count". For example, the name of first group would be "Group 1", second would be "Group 2" and so on.
          // Note that a personal chat is not considered a group and the count is not updated for personal chats.
          // If group is successfully created, return group.

          if(users.size() == 2) return this.createPersonalChat(users);

          this.customGroupCount++;
          String groupName = "Group " + this.customGroupCount;
          Group group = new Group(groupName, users.size());
          groupUserMap.put(group, users);
          adminMap.put(group, users.get(0));
          return group;
     }

     public Group createPersonalChat(List<User> users) {
          String groupName = users.get(1).getName();
          Group personalGroup = new Group(groupName, 2);
          groupUserMap.put(personalGroup, users);
          return personalGroup;
     }

     public int createMessage(String content){
          // The 'i^th' created message has message id 'i'.
          // Return the message id.

          this.messageId++;
          Message message = new Message(messageId, content, new Date());
          return this.messageId;
     }

     public int sendMessage(Message message, User sender, Group group) throws Exception{
          //Throw "Group does not exist" if the mentioned group does not exist
          //Throw "You are not allowed to send message" if the sender is not a member of the group
          //If the message is sent successfully, return the final number of messages in that group.


          if(!groupUserMap.containsKey(group)) throw new Exception("Group does not exist");
          if(!this.userExistsInGroup(group, sender)) throw  new Exception("You are not allowed to send message");

          List<Message> messages = new ArrayList<>();
          if(groupMessageMap.containsKey(group)) messages = groupMessageMap.get(group);

          messages.add(message);
          groupMessageMap.put(group, messages);
          return messages.size();
     }

     public boolean userExistsInGroup(Group group, User sender) {
          List<User> users = groupUserMap.get(group);
          for(User user: users) {
               if(user.equals(sender)) return true;
          }

          return false;
     }
     public int removeUser(User user) throws Exception{
          //A user belongs to exactly one group
          //If user is not found in any group, throw "User not found" exception
          //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
          //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
          //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall message.
          if(!groupUserMap.containsValue(user)) throw new Exception("User not found");
          User admin=userData.get(0);
          if(groupUserMap.containsValue(user) && userData.containsValue(admin)) throw new Exception("Cannot remove admin");
          List<Message> messages = new ArrayList<>();
          if(groupMessageMap.containsValue(user)) messages = groupMessageMap.get(messages);

          if(!userData.containsValue(admin))  groupUserMap.remove(user); groupMessageMap.remove(messages);
          return messages.size();


     }
}
