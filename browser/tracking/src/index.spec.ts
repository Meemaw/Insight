import { sum } from 'index';

describe('sum', () => {
  it('should sum positive values', () => {
    expect(sum(1, 2)).toEqual(3);
  });
});
