package com.meemaw.auth.org.invite.service;

import com.meemaw.auth.org.invite.model.CanInviteSend;
import com.meemaw.auth.org.invite.model.dto.InviteAcceptDTO;
import com.meemaw.auth.org.invite.model.dto.InviteCreateIdentifiedDTO;
import com.meemaw.auth.org.invite.model.dto.InviteDTO;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface InviteService {

  CompletionStage<InviteDTO> create(InviteCreateIdentifiedDTO teamInviteCreate);

  CompletionStage<Boolean> accept(InviteAcceptDTO teamInviteAccept);

  CompletionStage<Void> send(UUID token, CanInviteSend canInviteCreate);
}
