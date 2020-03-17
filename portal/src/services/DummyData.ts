export const _randomDate = (
  start: Date,
  end: Date
): { value: number; dateFormatted: string } => {
  const date: Date = new Date(
    start.getTime() + Math.random() * (end.getTime() - start.getTime())
  );
  return {
    value: date.valueOf(),
    dateFormatted: date.toLocaleDateString()
  };
};

export const _randomEmployeeName = (): string => {
  var candidates = "Anna Radek Bartek Mikołaj".split(" ");

  return candidates[Math.floor(Math.random() * candidates.length)];
};

export const _randomCustomerName = () : string => {
  var candidates = "Renoma Zgoda Medmax OrtMed".split(" ");

  return candidates[Math.floor(Math.random() * candidates.length)];
}