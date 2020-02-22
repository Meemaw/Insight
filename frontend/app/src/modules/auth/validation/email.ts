import * as Yup from 'yup';

export const emailSchema = Yup.string()
  .email('Please enter a valid email address.')
  .required('Required.');
