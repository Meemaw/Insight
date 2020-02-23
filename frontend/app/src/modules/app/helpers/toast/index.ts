import { Position, Toaster, Intent, IToaster } from '@blueprintjs/core';

let AppToaster: IToaster | undefined;

const getToaster = (): IToaster => {
  if (AppToaster === undefined) {
    AppToaster = Toaster.create({
      className: 'app-toaster',
      position: Position.BOTTOM_RIGHT,
    });
  }

  return AppToaster;
};

export const successToast = (message: string) => {
  getToaster().show({
    message,
    intent: Intent.SUCCESS,
    icon: 'endorsed',
  });
};

export const errorToast = (message: string) => {
  getToaster().show({
    message,
    intent: Intent.DANGER,
    icon: 'info-sign',
  });
};
