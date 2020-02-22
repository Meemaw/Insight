import { useFormik } from 'formik';
import * as Yup from 'yup';
import { emailSchema } from 'modules/auth/validation/email';
import { APIErrorDataResponse } from 'api';
import PasswordApi from 'api/password';
import { useState } from 'react';

const PasswordForgotSchema = Yup.object().shape({
  email: emailSchema,
});

const usePasswordForgot = () => {
  const [formError, setFormError] = useState<string | undefined>();
  const [checkYourInbox, setCheckYourInbox] = useState(false);

  const formik = useFormik({
    initialValues: {
      email: '',
    },
    onSubmit: (values, { setSubmitting }) => {
      PasswordApi.forgot(values.email)
        .then(response => setCheckYourInbox(response.data))
        .catch(async error => {
          const errorDTO: APIErrorDataResponse = await error.response.json();
          setFormError(errorDTO.error.message);
        })
        .finally(() => setSubmitting(false));
    },
    validationSchema: PasswordForgotSchema,
  });

  return { checkYourInbox, formError, ...formik };
};

export type PasswordForgotFormConfig = ReturnType<typeof usePasswordForgot>;

export default usePasswordForgot;
