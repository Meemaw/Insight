export type InsightIdentity = {
  orgId: string;
  uid: string;
  sessionId: string;
  host: string;
  expiresSeconds: number;
};

export type Cookie = Partial<InsightIdentity>;
