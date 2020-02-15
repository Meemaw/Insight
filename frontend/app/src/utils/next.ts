import { NextPageContext } from 'next';
import { IncomingMessage, ServerResponse } from 'http';

type ServerNextPageContext = NextPageContext & {
  req: IncomingMessage;
  res: ServerResponse;
};

export const isServer = (
  ctx: NextPageContext
): ctx is ServerNextPageContext => {
  return ctx.req !== undefined && ctx.res !== undefined;
};
