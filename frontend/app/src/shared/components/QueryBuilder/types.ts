import { Option } from 'baseui/select';

export type FilterType = 'click';

export type FilterableField = Option & {
  type: FilterType;
};

type ClickOperator = '*' | 'is' | 'selector';

export type Operator = ClickOperator;

type FilterField = {
  id: Operator;
  label: string;
};

export type OperatorOption = Omit<Option, 'id' | 'label'> & FilterField;

const WILDCARD_OPERATOR: OperatorOption = { id: '*', label: 'Anything' };
const TEXT_IS_OPEARTOR: OperatorOption = { id: 'is', label: 'Text is exactly' };
const SELECTOR_IS_OPEARTOR: OperatorOption = {
  id: 'selector',
  label: 'CSS selector',
};

const SUPPORTED_CLICK_OPERATORS = [
  WILDCARD_OPERATOR,
  TEXT_IS_OPEARTOR,
  SELECTOR_IS_OPEARTOR,
];

const SUPPORTED_OPERATORS: Record<FilterType, Option[]> = {
  click: SUPPORTED_CLICK_OPERATORS,
};

export const getSupportedOperators = (filterType: FilterType): Option[] => {
  return SUPPORTED_OPERATORS[filterType];
};

export const supportsInputs = (operator: Operator) => {
  return operator !== '*';
};

export type Filter = FilterableField & {
  id: string;
};
