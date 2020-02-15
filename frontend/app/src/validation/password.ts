import * as Yup from 'yup';

export const passwordSchema = Yup.string()
  .min(8, 'Password should be at least 8 characters long.')
  .required('Required.');
