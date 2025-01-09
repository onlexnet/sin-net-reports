import React from "react";
import { Radio, Button, Space } from "antd";
import _ from "lodash";

interface MainViewMultipleProjectsProps {
    projects: { id: string, name: string }[]
    projectSelected: (id: string) => void
}
export const MainViewMultipleProjects: React.FC<MainViewMultipleProjectsProps> = props => {

    const options = _.chain(props.projects)
        .map(it => ({ label: it.name, value: it.id }))
        .value();

    const [selected, setSelected] = React.useState<string | undefined>();

    return (
        <Space direction="vertical" align="center">
            <Radio.Group onChange={e => setSelected(e.target.value)}>
                <Space direction="vertical">
                    {options.map(option => (
                        <Radio key={option.value} value={option.value}>
                            {option.label}
                        </Radio>
                    ))}
                </Space>
            </Radio.Group>
            <Button type="primary" disabled={!selected} onClick={() => props.projectSelected(selected!)}>Kontunuuj pracę z wybranym projektem</Button>
        </Space>
    );
}

