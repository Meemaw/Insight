import useElementState from 'shared/hooks/useElementState';

function useHover<T extends Element>() {
  return useElementState<T>({
    truthyEventType: 'mouseover',
    falsyEventType: 'mouseout',
  });
}

export default useHover;
