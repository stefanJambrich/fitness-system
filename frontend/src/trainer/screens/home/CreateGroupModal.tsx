import { Alert, Button, Group, Modal, NumberInput, Stack, Text, Textarea, TextInput } from '@mantine/core'
import { useForm } from '@mantine/form'
import { useState } from 'react'
import type { CreateGroupRequest } from '../../../api/generated'
import { strings } from '../../../common/strings'
import { errorMessage } from '../../../common/utils/errorMessage'

type Props = {
  opened: boolean
  onClose: () => void
  onCreate: (request: CreateGroupRequest) => Promise<void>
}

type Values = {
  name: string
  description: string
  defaultCancelHours: number | string
}

const NAME_MAX = 80
const DESCRIPTION_MAX = 1000
const CANCEL_HOURS_MAX = 720
const CREATE_ERRORS = { VALIDATION: strings.groupFormInvalid }
const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone

const isValidName = (name: string): boolean => {
  const trimmed = name.trim()
  return trimmed.length >= 1 && trimmed.length <= NAME_MAX && !/\p{Cc}/u.test(trimmed)
}

const isValidCancelHours = (hours: number | string): boolean =>
  typeof hours === 'number' && Number.isInteger(hours) && hours >= 0 && hours <= CANCEL_HOURS_MAX

export const CreateGroupModal = ({ opened, onClose, onCreate }: Props) => {
  const [error, setError] = useState<string>()
  const [submitting, setSubmitting] = useState(false)
  const form = useForm<Values>({
    mode: 'uncontrolled',
    initialValues: { name: '', description: '', defaultCancelHours: 0 },
    validate: {
      name: (value) => (isValidName(value) ? null : strings.groupNameInvalid),
      defaultCancelHours: (value) => (isValidCancelHours(value) ? null : strings.groupCancelHoursInvalid),
    },
  })

  const close = () => {
    form.reset()
    setError(undefined)
    onClose()
  }

  const submit = async (values: Values) => {
    setSubmitting(true)
    setError(undefined)
    try {
      await onCreate({
        name: values.name.trim(),
        description: values.description.trim() || undefined,
        timezone,
        defaultCancelHours: Number(values.defaultCancelHours),
      })
      close()
    } catch (e) {
      setError(await errorMessage(e, CREATE_ERRORS))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Modal opened={opened} onClose={close} title={strings.groupCreateTitle} centered>
      <form onSubmit={form.onSubmit(submit)} noValidate>
        <Stack>
          <TextInput
            label={strings.groupNameLabel}
            maxLength={NAME_MAX}
            data-autofocus
            key={form.key('name')}
            {...form.getInputProps('name')}
          />
          <Textarea
            label={strings.groupDescriptionLabel}
            maxLength={DESCRIPTION_MAX}
            autosize
            minRows={2}
            maxRows={6}
            key={form.key('description')}
            {...form.getInputProps('description')}
          />
          <NumberInput
            label={strings.groupCancelHoursLabel}
            description={strings.groupCancelHoursHint}
            min={0}
            max={CANCEL_HOURS_MAX}
            allowDecimal={false}
            allowNegative={false}
            key={form.key('defaultCancelHours')}
            {...form.getInputProps('defaultCancelHours')}
          />
          <Text size="sm" c="dimmed">{strings.groupTimezoneNote(timezone)}</Text>
          {error && <Alert color="red">{error}</Alert>}
          <Group justify="flex-end">
            <Button variant="default" onClick={close} disabled={submitting}>{strings.cancel}</Button>
            <Button type="submit" loading={submitting}>{strings.groupCreate}</Button>
          </Group>
        </Stack>
      </form>
    </Modal>
  )
}