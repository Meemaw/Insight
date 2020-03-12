import { useEffect, useRef } from 'react';
import sinon, { SinonSandbox } from 'sinon';

export type SetupMocks<T> = (sandbox: SinonSandbox) => T;

const useMocks = <T>(setupMocks: SetupMocks<T>) => {
  const sandbox = sinon.createSandbox();
  const didSetupMocks = useRef(false);
  if (!didSetupMocks.current) {
    setupMocks(sandbox);
  }
  didSetupMocks.current = true;

  useEffect(() => {
    return () => {
      sandbox.restore();
    };
  }, [sandbox]);
};

export default useMocks;
