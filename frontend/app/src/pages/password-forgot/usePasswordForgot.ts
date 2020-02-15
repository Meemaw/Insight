import { useFormik } from 'formik';
import * as Yup from 'yup';
import { emailSchema } from 'validation/email';

export const PasswordForgotSchema = Yup.object().shape({
  email: emailSchema,
});

const usePasswordForgot = () => {
  const formik = useFormik({
    initialValues: {
      email: '',
    },
    onSubmit: values => {
      console.log(values);
    },
    validationSchema: PasswordForgotSchema,
  });

  return formik;
};

export default usePasswordForgot;
