import styled from 'styled-components';
import AuthLayout from 'modules/auth/components/Layout';

export const StyledForgotPassword = styled(AuthLayout)`
  form {
    div.with-action {
      position: relative;

      .action {
        position: absolute;
        top: 0;
        right: 0;
      }
    }
  }
`;
