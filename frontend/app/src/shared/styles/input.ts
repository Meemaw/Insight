import { Theme } from 'baseui/theme';
import { InputOverrides } from 'baseui/input';

const inputBorderRadius = (theme: Theme) => {
  return {
    borderBottomRightRadius: theme.sizing.scale100,
    borderTopRightRadius: theme.sizing.scale100,
    borderTopLeftRadius: theme.sizing.scale100,
    borderBottomLeftRadius: theme.sizing.scale100,
  };
};

export const createInputOverrides = (theme: Theme): InputOverrides => {
  const inputBorders = inputBorderRadius(theme);
  return {
    InputContainer: { style: inputBorders },
    Input: { style: inputBorders },
  };
};
