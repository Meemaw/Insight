import React from 'react';
import { Select, Value, SelectProps } from 'baseui/select';
import Flex from 'shared/components/flex/Flex';
import { useStyletron } from 'baseui';
import { Input, SIZE } from 'baseui/input';
import { v4 as uuid } from 'uuid';
import { Tag, KIND } from 'baseui/tag';
import { Delete } from 'baseui/icon';
import VerticalAligned from 'shared/components/flex/VerticalAligned';
import { StyleObject } from 'styletron-react';

import QuertFilterRowActions from '../QuertFilterRowActions';
import {
  Filter,
  FilterableField,
  Operator,
  getSupportedOperators,
  supportsInputs,
} from '../types';

type InputValue = {
  id: string;
  value: string;
};

type Props = {
  filter: Filter;
  fields: FilterableField[];
  onAddFilter: () => void;
  onDeleteFilter: (id: string) => void;
  showActionsOnHover?: boolean;
  size?: SIZE[keyof SIZE];
};

const QueryFilter = ({
  filter,
  fields,
  onAddFilter,
  onDeleteFilter,
  showActionsOnHover = true,
  size,
}: Props) => {
  const [_css, theme] = useStyletron();
  const [fieldValue, setFieldValue] = React.useState<Value>([filter]);
  const { scale200, scale100: gutter } = theme.sizing;
  const inputPlaceholderText = 'Type something';
  const operators = getSupportedOperators(filter.type);
  const [operatorValue, setOperatorValue] = React.useState<Value>([
    operators[0],
  ]);
  const selectedOperator = operatorValue[0].id as Operator;
  const [inputValues, setInputValues] = React.useState<InputValue[]>(() => [
    { id: uuid(), value: '' },
  ]);

  const onAddInput = React.useCallback(() => {
    setInputValues((prev) => [...prev, { id: uuid(), value: '' }]);
  }, []);

  const onDelete = React.useCallback(() => onDeleteFilter(filter.id), [
    onDeleteFilter,
    filter.id,
  ]);

  const borderRadiusInputProperties: StyleObject = {
    borderTopLeftRadius: scale200,
    borderBottomLeftRadius: scale200,
  };

  const borderRadiusEndEnchancerProperties: StyleObject = {
    borderTopRightRadius: scale200,
    borderBottomRightRadius: scale200,
  };

  const borderRadiusProperties: StyleObject = {
    ...borderRadiusInputProperties,
    ...borderRadiusEndEnchancerProperties,
  };

  const selectBaseProps: SelectProps = {
    size,
    searchable: false,
    clearable: false,
    overrides: {
      Root: { style: { width: 'fit-content', padding: gutter } },
      ControlContainer: { style: borderRadiusProperties },
    },
  };

  return (
    <Flex $style={{ ':hover': { background: theme.colors.accent50 } }}>
      <Flex $style={{ marginRight: gutter }}>
        <Select
          {...selectBaseProps}
          options={fields}
          value={fieldValue}
          onChange={(params) => setFieldValue(params.value)}
        />
        <Select
          {...selectBaseProps}
          options={operators}
          value={operatorValue}
          onChange={(params) => setOperatorValue(params.value)}
        />
      </Flex>
      <Flex $style={{ width: '100%', flexWrap: 'wrap' }}>
        {supportsInputs(selectedOperator) &&
          inputValues.map(({ value, id }, index) => {
            const isLastInput = index === inputValues.length - 1;
            const updateInputValue = (newValue: InputValue) => {
              setInputValues((prev) =>
                prev.map((v) => (v.id === id ? newValue : v))
              );
            };

            const onRemoveInput = () => {
              setInputValues((prev) => prev.filter((v) => v.id !== id));
            };

            const endEnhancer = isLastInput ? (
              <Tag closeable={false} onClick={onAddInput} kind={KIND.positive}>
                or
              </Tag>
            ) : (
              <Tag
                closeable={false}
                onClick={onRemoveInput}
                kind={KIND.negative}
              >
                <Delete />
              </Tag>
            );

            return (
              <Flex key={id}>
                <Input
                  size={size}
                  value={value}
                  placeholder={inputPlaceholderText}
                  endEnhancer={endEnhancer}
                  onChange={(e) =>
                    updateInputValue({ id, value: e.currentTarget.value })
                  }
                  overrides={{
                    Root: { style: { width: 'fit-content', padding: gutter } },
                    InputContainer: { style: borderRadiusInputProperties },
                    EndEnhancer: { style: borderRadiusEndEnchancerProperties },
                  }}
                />
                {!isLastInput && (
                  <VerticalAligned
                    $style={{
                      color: theme.colors.primary600,
                      margin: `0 ${theme.sizing.scale300}`,
                    }}
                  >
                    or
                  </VerticalAligned>
                )}
              </Flex>
            );
          })}
      </Flex>

      <VerticalAligned
        $style={{
          maxHeight: '40px',
          marginRight: gutter,
          opacity: showActionsOnHover ? 0 : undefined,
          ':hover': { opacity: 1 },
        }}
      >
        <QuertFilterRowActions
          onPlus={onAddFilter}
          onDelete={onDelete}
          size={size}
        />
      </VerticalAligned>
    </Flex>
  );
};

export default QueryFilter;
