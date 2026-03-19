import React from "react";
import { Button } from "components/ui/button";
import { Space } from "components/ui/layout";
import _ from "lodash";

interface MainViewMultipleProjectsProps {
    projects: { id: string, name: string }[];
    projectSelected: (id: string) => void;
}

export const MainViewMultipleProjects: React.FC<MainViewMultipleProjectsProps> = props => {
    const options = _.chain(props.projects)
        .map(it => ({ label: it.name, value: it.id }))
        .value();

    const [selected, setSelected] = React.useState<string | undefined>();

    return (
        <Space direction="vertical" align="center">
            <div className="mt-4 flex flex-col items-start">
                <div className="mb-4 mt-4 flex w-full flex-col items-start gap-2">
                    {options.map(option => (
                        <label key={option.value} className="flex w-full cursor-pointer items-center justify-start gap-2 text-left text-sm">
                            <input
                                type="radio"
                                name="project"
                                value={option.value}
                                checked={selected === option.value}
                                onChange={() => setSelected(option.value)}
                                className="h-4 w-4 accent-primary"
                            />
                            {option.label}
                        </label>
                    ))}
                </div>
                <Button disabled={!selected} onClick={() => props.projectSelected(selected!)}>
                    Kontynuuj pracę z wybranym projektem
                </Button>
            </div>
        </Space>
    );
};

