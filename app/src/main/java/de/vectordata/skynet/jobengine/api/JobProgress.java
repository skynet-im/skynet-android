package de.vectordata.skynet.jobengine.api;

import java.util.Objects;

public class JobProgress {

    private static final JobProgress INDETERMINATE = new JobProgress(0, true);

    private int progress;

    private boolean indeterminate;

    public JobProgress(int progress) {
        this.progress = progress;
    }

    private JobProgress(int progress, boolean indeterminate) {
        this.progress = progress;
        this.indeterminate = indeterminate;
    }

    public static JobProgress indeterminate() {
        return INDETERMINATE;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isIndeterminate() {
        return indeterminate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobProgress that = (JobProgress) o;
        return progress == that.progress &&
                indeterminate == that.indeterminate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(progress, indeterminate);
    }

}
