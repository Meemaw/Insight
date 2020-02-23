import styled from 'styled-components';
import Centered from 'shared/components/flex/Centered';
import { Colors } from '@blueprintjs/core';

const AuthLayout = styled(Centered)`
  width: 100%;
  height: 100%;
  background: ${Colors.DARK_GRAY4};

  form {
    max-width: 600px;
    width: 100%;

    @media screen and (max-width: 616px) {
      padding: 16px;
    }

    input,
    button {
      width: 100%;
    }

    p.error {
      text-align: center;
      margin-top: 8px;
      color: ${Colors.RED5};
    }
  }
`;

export default AuthLayout;
