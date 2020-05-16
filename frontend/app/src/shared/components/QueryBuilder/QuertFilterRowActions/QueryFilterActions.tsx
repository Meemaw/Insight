import React from 'react';
import Flex from 'shared/components/flex/Flex';
import { useStyletron } from 'baseui';
import { KIND, Button } from 'baseui/button';
import { Plus, Delete } from 'baseui/icon';
import { SIZE } from 'baseui/input';

type Props = {
  onPlus: (event: React.MouseEvent<HTMLButtonElement, MouseEvent>) => void;
  onDelete: (event: React.MouseEvent<HTMLButtonElement, MouseEvent>) => void;
  size?: SIZE[keyof SIZE];
};

const QueryFilterRowActions = ({ onPlus, onDelete, size }: Props) => {
  const [_css, theme] = useStyletron();

  return (
    <Flex
      $style={{
        height: theme.sizing.scale900,
        marginLeft: theme.sizing.scale1400,
      }}
    >
      <Button size={size} kind={KIND.minimal} onClick={onPlus}>
        <Plus />
      </Button>
      <Button size={size} kind={KIND.minimal} onClick={onDelete}>
        <Delete />
      </Button>
    </Flex>
  );
};

export default React.memo(QueryFilterRowActions);
