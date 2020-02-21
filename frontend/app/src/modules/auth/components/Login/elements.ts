import styled from 'styled-components';
import { Colors } from '@blueprintjs/core';
import AuthLayout from 'modules/auth/components/Layout';

export const StyledLogin = styled(AuthLayout)`
  form {
    div.with-action {
      position: relative;

      .action {
        position: absolute;
        top: 0;
        right: 0;
      }
    }

    .sso {
      margin: 32px 0;

      button {
        background-color: ${Colors.INDIGO1} !important;
      }

      button:hover {
        background-color: ${Colors.INDIGO3} !important;
      }
    }
  }
`;
