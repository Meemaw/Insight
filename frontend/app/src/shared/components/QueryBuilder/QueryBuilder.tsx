import React from 'react';
import { SIZE } from 'baseui/input';
import { v4 as uuid } from 'uuid';
import { Block } from 'baseui/block';

import { FilterableField, Filter } from './types';
import QueryFilter from './QueryFilter';

type Props = {
  fields: FilterableField[];
  size?: SIZE[keyof SIZE];
};

const QueryBuilder = ({ fields, size = 'mini' }: Props) => {
  const [filters, setFilters] = React.useState<Filter[]>([
    { ...fields[0], id: uuid() },
  ]);

  const onAddFilter = React.useCallback(() => {
    setFilters((prev) => [...prev, { ...fields[0], id: uuid() }]);
  }, [setFilters, fields]);

  const onRemoveFilter = React.useCallback(
    (id: string) => {
      setFilters((prev) =>
        prev.length <= 1 ? prev : prev.filter((f) => f.id !== id)
      );
    },
    [setFilters]
  );

  return (
    <Block $style={{ width: '100%' }}>
      {filters.map((filter, index) => {
        return (
          <QueryFilter
            fields={fields}
            filter={filter}
            size={size}
            key={filter.id}
            onAddFilter={onAddFilter}
            onDeleteFilter={onRemoveFilter}
            showActionsOnHover={index !== 0}
          />
        );
      })}
    </Block>
  );
};

export default QueryBuilder;
