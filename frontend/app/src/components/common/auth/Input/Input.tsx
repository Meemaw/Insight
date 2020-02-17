import React from 'react';

type ReactInputProps = React.DetailedHTMLProps<
  React.InputHTMLAttributes<HTMLInputElement>,
  HTMLInputElement
>;

type Props = ReactInputProps & {
  error?: string;
};

const Input = ({ error, disabled, ...inputProps }: Props) => {
  const color = disabled ? '#96A2AC' : 'inherit';

  return (
    <div style={{ position: 'relative' }}>
      <input
        {...inputProps}
        disabled={disabled}
        style={{
          padding: 32,
          border: 0,
          width: '100%',
          fontSize: 16,
          boxSizing: 'border-box',
          color,
        }}
      />
      {error && (
        <span
          className="validation-error"
          style={{
            color: '#E35F61',
            position: 'absolute',
            fontSize: 12,
            bottom: 10,
            left: 32,
          }}
        >
          {error}
        </span>
      )}
    </div>
  );
};

export default React.memo(Input);
