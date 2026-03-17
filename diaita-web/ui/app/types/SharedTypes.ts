export type Stat = {
  title: string;
  icon: string;
  value: number | string;
  variation: number;
  formatter?: (value: number) => string;
};

export type Period = "daily" | "weekly" | "monthly";

export type Range = {
  start: Date;
  end: Date;
};
