import React, { useEffect } from 'react';
import { InputGroup, Popover, Tag } from '@blueprintjs/core';
import useFocus from 'shared/hooks/useFocus';

const GlobalSearch = () => {
  const [callbackRef, inputRef, focusActive] = useFocus<HTMLInputElement>();
  const width = focusActive ? 420 : 280;

  useEffect(() => {
    const handler = (event: KeyboardEvent) => {
      if (event.keyCode === 191 && inputRef.current) {
        event.stopPropagation();
        event.preventDefault();
        inputRef.current.focus();
      }
    };

    document.addEventListener('keydown', handler);

    return () => {
      document.removeEventListener('keydown', handler);
    };
  });

  return (
    <Popover
      isOpen={focusActive}
      content={
        <div style={{ width }}>
          {[1, 2, 3, 4, 5].map(item => {
            return (
              <div key={item} style={{ padding: 16, cursor: 'pointer' }}>
                item
              </div>
            );
          })}
        </div>
      }
      modifiers={{ arrow: { enabled: false } }}
      usePortal={false}
    >
      <InputGroup
        style={{ width, transition: 'width 0.2s ease' }}
        inputRef={callbackRef}
        placeholder="Search Insights ..."
        leftIcon="search"
        rightElement={
          <Tag minimal style={{ textAlign: 'center' }}>
            /
          </Tag>
        }
      />
    </Popover>
  );
};

export default GlobalSearch;
