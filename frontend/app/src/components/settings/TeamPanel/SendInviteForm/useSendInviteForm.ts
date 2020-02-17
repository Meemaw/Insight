import { useFormik } from 'formik';
import * as api from 'api';
import { errorToast, successToast } from 'components/common/app/Toaster';
import { emailSchema } from 'validation/email';
import * as Yup from 'yup';

type HTTPError = {
  response: Response;
};

const SendInviteSchema = Yup.object().shape({
  email: emailSchema,
});

const useSendInvite = (createInvite: typeof api.createInvite) => {
  const formik = useFormik({
    initialValues: {
      email: '',
      role: 'ADMIN' as api.UserRole,
    },
    onSubmit: (values, { setSubmitting }) => {
      createInvite(values.role, values.email)
        .then(() => successToast(`Invite email sent to ${values.email}`))
        .catch((error: HTTPError) => {
          const errorMessage =
            error.response.status === 409
              ? 'This user has already been invited'
              : 'Something went wrong';

          errorToast(errorMessage);
        })
        .finally(() => setSubmitting(false));
    },
    validationSchema: SendInviteSchema,
  });

  return formik;
};

export default useSendInvite;
