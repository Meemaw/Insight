/* eslint-disable jsx-a11y/no-noninteractive-element-interactions */
/* eslint-disable jsx-a11y/click-events-have-key-events */
import React, { useEffect, useCallback } from 'react';
import { InputGroup, Popover, Tag } from '@blueprintjs/core';
import useFocus from 'shared/hooks/useFocus';

const KEY_CODE = {
  ESC: 27,
  SLASH: 191,
} as const;

const GlobalSearch = () => {
  const [active, onInputRef, inputRef] = useFocus<HTMLInputElement>();
  const width = active ? 420 : 280;

  const focus = useCallback(() => {
    if (inputRef.current) {
      inputRef.current.focus();
    }
  }, []);

  const blur = useCallback(() => {
    if (inputRef.current) {
      inputRef.current.blur();
    }
  }, []);

  useEffect(() => {
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.keyCode === KEY_CODE.SLASH) {
        event.stopPropagation();
        event.preventDefault();
        focus();
      } else if (event.keyCode === KEY_CODE.ESC) {
        blur();
      }
    };

    document.addEventListener('keydown', onKeyDown);

    return () => {
      document.removeEventListener('keydown', onKeyDown);
    };
  });

  const content = (
    <ul style={{ width, margin: 0 }}>
      {[1, 2, 3, 4, 5].map(item => {
        return (
          <li
            onMouseDown={event => {
              event.preventDefault();
            }}
            onClick={blur}
            key={item}
            style={{
              padding: 16,
              margin: 0,
              cursor: 'pointer',
              listStyle: 'none',
            }}
          >
            item
          </li>
        );
      })}
    </ul>
  );

  return (
    <Popover
      isOpen={active}
      content={content}
      modifiers={{ arrow: { enabled: false } }}
      usePortal={false}
    >
      <InputGroup
        style={{ width, transition: 'width 0.2s ease' }}
        inputRef={onInputRef}
        placeholder="Search Insights ..."
        leftIcon="search"
        rightElement={
          <Tag minimal style={{ textAlign: 'center' }} onClick={focus}>
            /
          </Tag>
        }
      />
    </Popover>
  );
};

export default React.memo(GlobalSearch);
