import { login } from 'api';
import { useFormik } from 'formik';
import { emailSchema } from 'validation/email';
import { passwordSchema } from 'validation/password';
import * as Yup from 'yup';
import { useState } from 'react';
import { useRouter } from 'next/router';

type APIErrorDataResponse = {
  error: APIError;
};

type APIError = {
  statusCode: number;
  reason: string;
  message: string;
};

const LoginSchema = Yup.object().shape({
  email: emailSchema,
  password: passwordSchema,
});

const useLogin = () => {
  const router = useRouter();
  const [formError, setFormError] = useState<string | undefined>();

  const formik = useFormik({
    initialValues: {
      email: '',
      password: '',
    },
    onSubmit: (values, { setSubmitting }) => {
      setFormError(undefined);
      login(values.email, values.password)
        .then(response => {
          console.log(response);
          router.replace('/');
        })
        .catch(async error => {
          const errorDTO: APIErrorDataResponse = await error.response.json();
          setSubmitting(false);
          setFormError(errorDTO.error.message);
        });
    },
    validationSchema: LoginSchema,
  });

  return { ...formik, formError };
};

export default useLogin;
