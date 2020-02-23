import { Selector } from 'testcafe';

import config from '../config';

fixture('<TestcafeBug />').page(`${config.baseURL}/testcafe-bug`);

test('Displays comments', async t => {
  const listSelector = Selector('li');

  await t
    .expect(listSelector.childElementCount)
    .eql(500, 'Should list 500 comments');
});
