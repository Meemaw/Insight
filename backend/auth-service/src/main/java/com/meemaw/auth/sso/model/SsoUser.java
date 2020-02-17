package com.meemaw.auth.model.sso;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.meemaw.auth.model.user.UserDTO;
import com.meemaw.auth.model.user.UserRole;
import java.io.IOException;
import java.util.UUID;

public class SsoUser extends UserDTO implements IdentifiedDataSerializable {


  public SsoUser() {
  }

  public SsoUser(UserDTO userDTO) {
    this.id = userDTO.getId();
    this.email = userDTO.getEmail();
    this.role = userDTO.getRole();
    this.org = userDTO.getOrg();
  }

  @Override
  public int getFactoryId() {
    return SsoDataSerializableFactory.ID;
  }

  @Override
  public int getClassId() {
    return SsoDataSerializableFactory.SSO_USER_CLASS_ID;
  }

  @Override
  public void writeData(ObjectDataOutput out) throws IOException {
    out.writeUTF(this.id.toString());
    out.writeUTF(this.email);
    out.writeUTF(this.role.toString());
    out.writeUTF(this.org);
  }

  @Override
  public void readData(ObjectDataInput in) throws IOException {
    this.id = UUID.fromString(in.readUTF());
    this.email = in.readUTF();
    this.role = UserRole.valueOf(in.readUTF());
    this.org = in.readUTF();
  }

  public UserDTO dto() {
    return new UserDTO(id, email, role, org);
  }

  public String getOrg() {
    return org;
  }
}
