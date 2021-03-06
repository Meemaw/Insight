import React from 'react';
import { Block } from 'baseui/block';
import { Paragraph3 } from 'baseui/typography';
import { APIError } from '@insight/types';
import { useStyletron } from 'baseui';

type Props = {
  error: APIError;
};

const FormError = ({ error }: Props) => {
  const [_css, theme] = useStyletron();
  return (
    <Block
      display="flex"
      justifyContent="center"
      marginTop={theme.sizing.scale600}
    >
      <Paragraph3 color={theme.colors.borderError}>{error.message}</Paragraph3>
    </Block>
  );
};

export default React.memo(FormError);
