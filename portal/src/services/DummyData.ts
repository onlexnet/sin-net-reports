
export const _randomDate = (start: Date, end: Date): { value: number; dateFormatted: string } => {
    const date: Date = new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()));
    return {
      value: date.valueOf(),
      dateFormatted: date.toLocaleDateString()
    };
  }

export const _randomUserName = () : string => {
    var candidates = [
        'Anna',
        'Radek',
        'Bartek',
        'Mikołaj'
    ]

    return candidates[Math.floor(Math.random() * candidates.length)];
  }
  