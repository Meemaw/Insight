import React from 'react';
import {
  InputGroup,
  Button,
  Intent,
  ButtonGroup,
  MenuItem,
  FormGroup,
} from '@blueprintjs/core';
import { Select } from '@blueprintjs/select';
import { InviteApi, UserRole } from 'api';
import SpacedBetween from 'components/common/flex/SpacedBetween';

import useSendInviteForm from './useSendInviteForm';
import { StyledSendInviteForm } from './elements';

type Props = {
  createInvite: typeof InviteApi.create;
  onCancel: (event: React.MouseEvent<HTMLElement, MouseEvent>) => void;
};

export const startCase = (param: string) => {
  return param.charAt(0).toUpperCase() + param.slice(1).toLowerCase();
};

const SendInviteForm = ({ createInvite, onCancel }: Props) => {
  const {
    handleSubmit,
    errors,
    handleChange,
    values,
    setFieldValue,
  } = useSendInviteForm(createInvite);

  return (
    <StyledSendInviteForm onSubmit={handleSubmit}>
      <SpacedBetween className="inputs">
        <FormGroup
          style={{ width: '100%' }}
          helperText={errors.email}
          intent={errors.email ? Intent.DANGER : undefined}
        >
          <InputGroup
            name="email"
            placeholder="Email address"
            autoFocus
            intent={errors.email ? Intent.DANGER : undefined}
            onChange={handleChange}
            value={values.email}
            fill
          />
        </FormGroup>

        <Select<UserRole>
          items={['ADMIN', 'STANDARD']}
          filterable={false}
          itemRenderer={(role, { handleClick, modifiers }) => (
            <MenuItem
              key={role}
              text={startCase(role)}
              onClick={handleClick}
              active={modifiers.active}
            />
          )}
          onItemSelect={(role, event) => {
            if (event) {
              event.stopPropagation();
            }
            setFieldValue('role', role);
          }}
          activeItem={values.role}
        >
          <Button
            text={startCase(values.role)}
            rightIcon="double-caret-vertical"
            intent={Intent.PRIMARY}
          />
        </Select>
      </SpacedBetween>

      <ButtonGroup className="actions">
        <Button intent={Intent.PRIMARY} type="submit">
          Send invitation
        </Button>
        <Button onClick={onCancel}>Cancel</Button>
      </ButtonGroup>
    </StyledSendInviteForm>
  );
};

export default SendInviteForm;
