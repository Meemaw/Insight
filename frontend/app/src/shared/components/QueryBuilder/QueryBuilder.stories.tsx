import React from 'react';

import QueryBuilder from './QueryBuilder';

export default {
  title: 'Shared|QueryBuilder',
};

export const Default = () => {
  return (
    <QueryBuilder
      fields={[
        { id: 'clicked', label: 'Clicked', type: 'click' },
        { id: 'rage_clicked', label: 'Rage clicked', type: 'click' },
        { id: 'error_clicked', label: 'Error clicked', type: 'click' },
        { id: 'dead_clicked', label: 'Dead clicked', type: 'click' },
      ]}
    />
  );
};
