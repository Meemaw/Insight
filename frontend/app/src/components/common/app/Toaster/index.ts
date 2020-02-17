import { Position, Toaster, Intent } from '@blueprintjs/core';

const AppToaster = Toaster.create({
  className: 'app-toaster',
  position: Position.BOTTOM_RIGHT,
});

export const successToast = (message: string) => {
  AppToaster.show({
    message,
    intent: Intent.SUCCESS,
    icon: 'endorsed',
  });
};

export const errorToast = (message: string) => {
  AppToaster.show({
    message,
    intent: Intent.DANGER,
    icon: 'info-sign',
  });
};
