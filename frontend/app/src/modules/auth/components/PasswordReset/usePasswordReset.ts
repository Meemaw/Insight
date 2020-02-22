import { useFormik } from 'formik';
import * as Yup from 'yup';
import { passwordSchema } from 'validation/password';
import { useState } from 'react';
import { APIErrorDataResponse } from 'api';
import PasswordApi, { PasswordResetRequestBase } from 'api/password';
import { useRouter } from 'next/router';

const PasswordResetSchema = Yup.object().shape({
  password: passwordSchema,
});

const usePasswordReset = (props: PasswordResetRequestBase) => {
  const router = useRouter();
  const [formError, setFormError] = useState<string | undefined>();

  const formik = useFormik({
    initialValues: {
      password: '',
    },
    onSubmit: (values, { setSubmitting }) => {
      PasswordApi.reset({ ...props, password: values.password })
        .then(_ => router.replace('/'))
        .catch(async error => {
          const errorDTO: APIErrorDataResponse = await error.response.json();
          setFormError(errorDTO.error.message);
        })
        .finally(() => setSubmitting(false));
    },
    validationSchema: PasswordResetSchema,
  });

  return { formError, ...formik };
};

export default usePasswordReset;
