import styled from 'styled-components';

export const StyledSendInviteForm = styled.form`
  padding: 12px;
  width: 400px;

  .inputs {
    display: flex;
    margin-bottom: 16px;

    > *:nth-child(1) {
      margin: 0 16px 0 0;
    }
  }

  .actions > button:nth-child(2) {
    margin-left: 16px;
  }
`;
