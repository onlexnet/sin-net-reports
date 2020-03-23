export const _randomDate = (
  start: Date,
  end: Date
): { value: Date; dateFormatted: string } => {
  const date: Date = new Date(
    start.getTime() + Math.random() * (end.getTime() - start.getTime())
  );
  return {
    value: date,
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

export const _randomDuration = () => Math.floor(Math.random() * 8) + 1;
export const _randomDistance = () => Math.floor(Math.random() * 50);
export const _dandomServiceDescription = () => {
  var candidates = "Poprawa danych,Wprowadzanie korekt,Odtwarzanie danych,konfiguracja środowisk".split(",");
  return candidates[Math.floor(Math.random() * candidates.length)];
}
