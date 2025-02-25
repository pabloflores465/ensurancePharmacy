export const useChanges: () => Ref<
  {
    fieldNames: string[];
    oldValue: object;
    newValue: object;
  }[]
> = () => useState("changes", () => []);

export const addChange = (
  fieldNames: string[],
  oldValue: object,
  newValue: object,
): void => {
  useChanges().value.push({ fieldNames, oldValue, newValue });
};

export const applyChange = (index: number) => {
  useChanges().value[index].oldValue = useChanges().value[index].newValue;
};
